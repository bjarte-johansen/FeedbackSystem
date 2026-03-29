package root.database;

public class DataSourceConnectionParams
{
    public String url;
    public String username;
    public String password;

    public DataSourceConnectionParams(String url, String username, String password)
    {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String toString()
    {
        return "DataSourceConnectionParams{url='" + url + "', username='" + username + "', password='[HIDDEN]'}";
    }
}