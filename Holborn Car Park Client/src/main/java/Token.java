public class Token {
    private boolean auth;
    private String token;

    /**
     * Empty constructor
     */
    public Token() {
        auth = false;
        token = null;
    }


    public Token(boolean auth, String token) {
        this.token = token;
        this.auth = auth;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    @Override
    public String toString() {
        return "Token{" +
                "auth=" + auth +
                ", token='" + token + '\'' +
                '}';
    }
}
