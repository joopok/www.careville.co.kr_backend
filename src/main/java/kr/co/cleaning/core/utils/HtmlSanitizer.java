package kr.co.cleaning.core.utils;

import java.util.regex.Pattern;

/**
 * 아주 기본적인 HTML Sanitizer.
 * - script/style/iframe 태그 제거
 * - on* 이벤트 핸들러 제거
 * - javascript: URL 제거
 * 완전한 보안을 위해서는 전용 라이브러리(OWASP Sanitizer/Jsoup 등) 사용 권장.
 */
public class HtmlSanitizer {

    private static final Pattern SCRIPT_TAG = Pattern.compile("(?is)<script.*?>.*?</script>");
    private static final Pattern STYLE_TAG = Pattern.compile("(?is)<style.*?>.*?</style>");
    private static final Pattern IFRAME_TAG = Pattern.compile("(?is)<iframe.*?>.*?</iframe>");
    private static final Pattern EVENT_ATTR = Pattern.compile("(?i)\\s+on[\\w-]+\\s*=\\s*\\\".*?\\\"|\\s+on[\\w-]+\\s*=\\s*'.*?'|\\s+on[\\w-]+\\s*=\\s*[^\\s>]+", Pattern.DOTALL);
    private static final Pattern JS_HREF = Pattern.compile("(?i)href\\s*=\\s*\\\"javascript:.*?\\\"|href\\s*=\\s*'javascript:.*?'|href\\s*=\\s*javascript:[^\\s>]+", Pattern.DOTALL);

    public static String sanitize(String html) {
        if (html == null || html.isEmpty()) return html;
        String out = html;
        out = SCRIPT_TAG.matcher(out).replaceAll("");
        out = STYLE_TAG.matcher(out).replaceAll("");
        out = IFRAME_TAG.matcher(out).replaceAll("");
        out = EVENT_ATTR.matcher(out).replaceAll("");
        out = JS_HREF.matcher(out).replaceAll("href=\"#\"");
        return out;
    }
}

