package org.candlepin;

public class CaseNormalizer implements LineModifier {

	@Override
	public String modify(String line) {
		if (line ==null)
			return line;
		
		line = line.replace(" uuid ", " UUID ");
		line = line.replace(" id ", " ID ");
		
		return line;
	}

}
