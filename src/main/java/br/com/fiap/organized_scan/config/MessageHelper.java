package br.com.fiap.organized_scan.config;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Locale;

@Component
public class MessageHelper {
    private final MessageSource messageSource;

    public MessageHelper(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /** Retorna a mensagem; se não achar a chave, retorna a própria chave. */
    public String get(String code, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        try {
            return messageSource.getMessage(code, args, locale);
        } catch (NoSuchMessageException e) {
            return code;
        }
    }

    /** Retorna a mensagem; se não achar, usa o default (formatável com args). */
    public String getOrDefault(String code, String defaultMessage, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        try {
            return messageSource.getMessage(code, args, locale);
        } catch (NoSuchMessageException e) {
            return defaultMessage != null ? MessageFormat.format(defaultMessage, args) : code;
        }
    }
}
