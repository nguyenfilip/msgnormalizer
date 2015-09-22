package org.candlepin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TicksNormalizer implements LineModifier {
	private Pattern pattern = Pattern.compile("''\\{(\\d)\\}''");

	@Override
	public String modify(String line) {
		if (line.contains("''"))
			System.out.println("TicksNormalizer Original: " + line);

		Matcher m = pattern.matcher(line);

		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "\\\\\"{" + m.group(1) + "}\\\\\"");
		}
		m.appendTail(sb);

		if (line.contains("''"))
			System.out.println("TicksNormalizer New: " + sb.toString());

		return sb.toString();
	}

}
