package app.personal_weather.data;

import org.json.JSONObject;

/**
 * Created by Christopher D. Harte on 12/06/2021.
 */
public class Condition
{
    private String text;
    private String icon;
    private String code;

    public String getText()
    {
        return text;
    }

    public String getIcon()
    {
        return icon;
    }

    public String getCode()
    {
        return code;
    }

    public void populate (JSONObject data)
    {
        text = data.optString("text");
        icon = data.optString("icon");
        code = data.optString("code");
    }
}
