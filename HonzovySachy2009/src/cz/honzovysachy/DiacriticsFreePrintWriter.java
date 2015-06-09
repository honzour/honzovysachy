package cz.honzovysachy;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class DiacriticsFreePrintWriter extends PrintWriter {
	
	public DiacriticsFreePrintWriter(String fileName)
			throws FileNotFoundException {
		super(fileName);
	}

	@Override
	public PrintWriter append(char c) {
		if (c < 128)
			return super.append(c);
		else
			return super.append('?');
	}

	@Override
	public PrintWriter append(CharSequence csq) {
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < csq.length(); i++)
		{
			char c = csq.charAt(i);
			if (c > 128)
				c = '?';
			sb.append(c);
		}
		return super.append(sb.toString());
	}
	
	

}
