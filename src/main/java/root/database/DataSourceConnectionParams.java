package root.database;

public record DataSourceConnectionParams(String url, String username, String password, String defaultSchema)
{
    @Override
    public String toString()
    {
        return "DataSourceConnectionParams{url='" + url + "', username='" + username + "', password='[HIDDEN]', defaultSchema='" + defaultSchema + "'}";
    }
}