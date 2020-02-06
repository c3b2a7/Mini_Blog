package me.lolicom.blog.web.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author lolicom
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class AjaxResponse {
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String msg;
    
    private boolean ok;
    
    private AjaxResponse(String msg, boolean ok) {
        this.msg = msg;
        this.ok = ok;
    }
    
    public static AjaxResponse ok() {
        return ok(null);
    }
    
    public static AjaxResponse ok(String msg) {
        return new AjaxResponse(msg, true);
    }
    
    public static AjaxResponse ok(String msg, Object... args) {
        return ok(String.format(msg, args));
    }
    
    public static AjaxResponse fail() {
        return fail(null);
    }
    
    
    public static AjaxResponse fail(String msg) {
        return new AjaxResponse(msg, false);
    }
    
    public static AjaxResponse fail(String msg, Object... args) {
        return fail(String.format(msg, args));
    }
    
}
