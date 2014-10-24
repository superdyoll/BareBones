/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package barebones;

/**
 *
 * @author lpp1g14
 */
public class LanguageException extends Exception {

    public LanguageException() {
        super();
    }

    public LanguageException(String message) {
        super(message);
    }

    public LanguageException(String message, Throwable cause) {
        super(message, cause);
    }

    public LanguageException(Throwable cause) {
        super(cause);
    }

}
