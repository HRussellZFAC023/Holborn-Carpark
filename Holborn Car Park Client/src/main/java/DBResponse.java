public class DBResponse {
    private int responseCode;
    private String body;

    public DBResponse(int responseCode, String body) {
        this.responseCode = responseCode;
        this.body = body;
    }

    public int getResponseCode() {
        return responseCode;
    }


    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "DBResponse{" +
                "responseCode=" + responseCode +
                ", body='" + body + '\'' +
                '}';
    }
}
