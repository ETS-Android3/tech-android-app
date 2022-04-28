package org.itsmng.androidapp.api.mgmt;

public class ApiEndpoint {

    /**
     * Note on endpoint : if string contain %s it mean a dynamic value will override the string upon REST Call
     */

    // Current full session
    public static final String API_GET_FULL_SESSION = "getFullSession/";

    // Ticket API Endpoint
    public static final String API_ROOT_TICKET = "Ticket/";
    public static final String API_UPDATE_TICKET = "Ticket/%s";
    public static final String API_GET_ALL_TICKETS = "Ticket/?expand_dropdowns=true&range=0-500";
    public static final String API_GET_MY_TICKETS = "Ticket/?expand_dropdowns=true&range=0-500";
    public static final String API_GET_TICKETS_DETAILS = "Ticket/%s?expand_dropdowns=true";

    // Task API Endpoint
    public static final String API_ROOT_TICKET_TASKS = "TicketTask/";
    public static final String API_GET_TICKET_TASKS = "TicketTask/?expand_dropdowns=true&searchText[tickets_id]=%s";

    // Ticket user Endpoint
    public static final String API_GET_TICKET_USERS = "Ticket_User/?expand_dropdowns=true&searchText[tickets_id]=%s&searchText[type]=2";
    public static final String API_GET_TICKET_ASSIGNED = "Ticket_User/?expand_dropdowns=false&searchText[users_id]=%s&searchText[type]=2";

    // Followup API Endpoint
    public static final String API_ROOT_TICKET_FOLLOWUPS = "ITILFollowup/";
    public static final String API_GET_TICKET_FOLLOWUPS = "ITILFollowup/?expand_dropdowns=true&searchText[items_id]=%s";

    // Location API Endpoint
    public static final String API_GET_LOCATIONS = "Location/";

    // ITILCategory API Endpoint
    public static final String API_GET_ITILCATEGORIES = "ITILCategory/";

    // Computer API Endpoint
    public static final String API_GET_ALL_COMPUTERS = "Computer/?expand_dropdowns=true&range=0-500";
    public static final String API_GET_ALL_COMPUTERS_BY_NAME = "Computer/?expand_dropdowns=true&searchText[name]=^%s$&range=0-500";
    public static final String API_GET_ONE_COMPUTER = "Computer/%s?expand_dropdowns=true";

    // Ticket items
    public static final String API_GET_ITEM_TICKETS = "Item_Ticket/&searchText[items_id]=%s";

}
