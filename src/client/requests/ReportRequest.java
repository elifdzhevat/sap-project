package client.requests;

public class ReportRequest {
    private String action = "report";
    private String reportType;
    private String dateFrom;
    private String dateTo;

    public ReportRequest(String reportType, String dateFrom, String dateTo) {
        this.reportType = reportType;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    public String getAction() {
        return action;
    }

    public String getReportType() {
        return reportType;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }
}
