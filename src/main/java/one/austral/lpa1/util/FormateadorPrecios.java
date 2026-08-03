package one.austral.lpa1.util;

import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.stereotype.Component;

@Component("precios")
public class FormateadorPrecios {

	public String formatear(int precioCop, Locale locale) {
		NumberFormat numeros = NumberFormat.getNumberInstance(locale);
		return "COP " + numeros.format(precioCop);
	}
}