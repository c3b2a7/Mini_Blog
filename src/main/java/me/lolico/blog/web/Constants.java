package me.lolico.blog.web;

/**
 * @author lolico
 */
public abstract class Constants {
    /**
     * 公开api前缀
     */
    public static final String PUBLIC_API = "/public";

    /**
     * 注册确认邮箱中的链接前缀
     */
    public static final String MAIL_CONFIRMATION_SCHEME = PUBLIC_API + "/confirm/";

    /**
     * 注册用户的邮箱确认消息内容
     */
    public static final String MAIL_CONFIRMATION_TEXT = "<h3>验证你的邮箱</h3><p>前往链接完成验证：</p><p><a href=\"http://%s\">http://%s</a></p>";
}
