package org.candlepin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NoQuotesNormalizer implements LineModifier {
	private Pattern pattern = Pattern.compile("(?<=[ \"])\\{(\\d)\\}(?=[\" \\.])");

	@Override
	public String modify(String line) {
		if (line.contains(" of ")) {
			System.out.println("NoQuotesNormalizer The line contains ' of ' sequence, skipping those");
			System.out.println("NoQuotesNormalizer: " + line);
			return line;
		}
		
		boolean matches = pattern.matcher(line).find();
		if (matches)
			System.out.println("NoQuotesNormalizer Original: " + line);
		
		Matcher m = pattern.matcher(line);

		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "\\\\\"{" + m.group(1) + "}\\\\\"");
		}
		m.appendTail(sb);
		
		if (matches)
			System.out.println("NoQuotesNormalizer New: " + sb.toString());

		return sb.toString();
	}

}
