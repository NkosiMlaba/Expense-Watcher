package weshare.server;

import weshare.controller.*;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

public class Routes {
    public static final String LOGIN_PAGE = "/";
    public static final String LOGIN_ACTION = "/login.action";
    public static final String LOGOUT = "/logout";
    public static final String EXPENSES = "/expenses";
    public static final String NEWEXPENSES = "/newexpense";
    public static final String EXPENSE_ACTION = "/expense.action";
    public static final String PAYMENT_REQUEST = "/paymentrequest";
    public static final String PAYMENT_REQUEST_ACTION = "/paymentrequest.action";
    public static final String PAYMENT_REQUEST_SENT = "/paymentrequests_sent";
    public static final String PAYMENT_REQUEST_RECEIVED_ACTION = "/payments.action";
    public static final String PAYMENT_REQUEST_RECEIVED_SENT = "/paymentrequests_received";


    public static void configure(WeShareServer server) {
        server.routes(() -> {
            post(LOGIN_ACTION,  PersonController.login);
            get(LOGOUT,         PersonController.logout);
            get(EXPENSES,           ExpensesController.view);
            get(NEWEXPENSES, ExpensesController.newExpense);
            post(EXPENSE_ACTION, ExpensesController.expenseAction);
            get(PAYMENT_REQUEST, ExpensesController.payment_request);
            post(PAYMENT_REQUEST_ACTION, ExpensesController.payment_request_Action);
            get(PAYMENT_REQUEST_SENT, ExpensesController.payment_request_sent);
            post(PAYMENT_REQUEST_RECEIVED_ACTION, ExpensesController.payment_request_Received_Action);
            get(PAYMENT_REQUEST_RECEIVED_SENT, ExpensesController.payment_request_Received_sent);
        });
    }
}
