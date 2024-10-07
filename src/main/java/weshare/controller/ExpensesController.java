package weshare.controller;

import io.javalin.http.Handler;
import weshare.model.*;
import weshare.persistence.ExpenseDAO;
import weshare.server.Routes;
import weshare.server.ServiceRegistry;
import weshare.server.WeShareServer;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;
import java.util.stream.Collectors;

import static weshare.model.MoneyHelper.ZERO_RANDS;

public class ExpensesController {

    public static final Handler view = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        Collection<Expense> expenses = expensesDAO.findExpensesForPerson(personLoggedIn);
        List<Expense> unpaidExpenses = expenses.stream()
                .filter(expense -> !expense.isFullyPaidByOthers())
                .collect(Collectors.toList());

        Map<String, Object> viewModel = new HashMap<>(Map.of("expenses", unpaidExpenses));
        MonetaryAmount totalAmount = calculateTotalAmount(unpaidExpenses);

        viewModel.put("total", totalAmount);
        context.render("expenses.html", viewModel);
    };

    public static Handler newExpense = context -> {
        context.render("newexpense.html");
    };

    public static final Handler expenseAction = context -> {
        ExpenseDAO expenseDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person person = WeShareServer.getPersonLoggedIn(context);

        String description = context.formParam("description");
        LocalDate date = LocalDate.parse(context.formParam("date"));
        MonetaryAmount amount = MoneyHelper.amountOf(Long.parseLong(context.formParam("amount")));

        Expense newExpense = new Expense(person, description, amount, date);
        expenseDAO.save(newExpense);
        context.sessionAttribute(WeShareServer.SESSION_USER_KEY, person);
        context.redirect(Routes.EXPENSES);
    };

    public static final Handler payment_request = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        UUID uuid = UUID.fromString(context.queryParam("expenseId"));
        Optional<Expense> expense = expensesDAO.get(uuid);

        expense.ifPresent(exp -> context.render("paymentrequest.html", Map.of("expense", exp)));
    };

    public static final Handler payment_request_sent = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person person = WeShareServer.getPersonLoggedIn(context);
        Collection<PaymentRequest> paymentRequests = expensesDAO.findPaymentRequestsSent(person);

        MonetaryAmount total = calculateTotalPaymentRequests(paymentRequests);
        context.render("paymentrequests_sent.html", Map.of("newexpenses", paymentRequests, "total", total));
    };

    public static final Handler payment_request_Action = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        UUID expenseId = UUID.fromString(context.formParam("expense_id"));
        Optional<Expense> optionalExpense = expensesDAO.get(expenseId);

        if (optionalExpense.isPresent()) {
            Expense expense = optionalExpense.get();
            Person person = new Person(context.formParam("email"));
            LocalDate dueDate = LocalDate.parse(context.formParam("due_date"), DateHelper.DD_MM_YYYY);

            MonetaryAmount amount = MoneyHelper.amountOf(Long.parseLong(context.formParam("amount")));
            expense.requestPayment(person, amount, dueDate);
            expensesDAO.save(expense);

            context.redirect("/paymentrequest?expenseId=" + expenseId.toString());
        } else {
            context.status(404).result("Expense not found.");
        }
    };


    public static final Handler payment_request_Received_Action = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        UUID paymentRequestId = UUID.fromString(context.formParam("payment_request_id"));
        Person person = WeShareServer.getPersonLoggedIn(context);
        expensesDAO.findPaymentRequestsReceived(person)
                .stream()
                .filter(pr -> pr.getId().equals(paymentRequestId))
                .findFirst()
                .ifPresent(pr -> {
                    pr.pay(person, DateHelper.TODAY);
                    Expense newExpense = new Expense(person, pr.getDescription(), pr.getAmountToPay(), DateHelper.TODAY);
                    expensesDAO.save(newExpense);
                    context.redirect(Routes.PAYMENT_REQUEST_RECEIVED_SENT);
                });
    };

    public static final Handler payment_request_Received_sent = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person person = WeShareServer.getPersonLoggedIn(context);
        Collection<PaymentRequest> paymentRequests = expensesDAO.findPaymentRequestsReceived(person);

        MonetaryAmount total = paymentRequests.stream()
                .filter(pr -> !pr.isPaid())
                .map(PaymentRequest::getAmountToPay)
                .reduce(ZERO_RANDS, MonetaryAmount::add);

        context.render("paymentrequests_received.html", Map.of("newexpenses", paymentRequests, "total", total));
    };

    private static MonetaryAmount calculateTotalAmount(List<Expense> expenses) {
        return expenses.stream()
                .map(expense -> expense.getAmount().subtract(expense.totalAmountForPaymentsReceived()))
                .reduce(ZERO_RANDS, MonetaryAmount::add);
    }

    private static MonetaryAmount calculateTotalPaymentRequests(Collection<PaymentRequest> paymentRequests) {
        return paymentRequests.stream()
                .map(PaymentRequest::getAmountToPay)
                .reduce(ZERO_RANDS, MonetaryAmount::add);
    }
}
