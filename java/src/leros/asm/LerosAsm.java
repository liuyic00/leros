/*
--
--  Copyright 2011 Martin Schoeberl <masca@imm.dtu.dk>,
--                 Technical University of Denmark, DTU Informatics. 
--  All rights reserved.
--
--  License: TBD, BSD style requested, decision pending.
--
 */

package leros.asm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;

import leros.asm.generated.*;

public class LerosAsm {

	static final int ADDRBITS = 8;
	static final int DATABITS = 32;
	static final int ROM_LEN = 1 << ADDRBITS;

	String fname;
	String dstDir = "./";
	String srcDir = "./";

	public LerosAsm(String[] args) {
		srcDir = System.getProperty("user.dir");
		dstDir = System.getProperty("user.dir");
		processOptions(args);
		if (!srcDir.endsWith(File.separator))
			srcDir += File.separator;
		if (!dstDir.endsWith(File.separator))
			dstDir += File.separator;
	}

	String bin(int val, int bits) {

		String s = "";
		for (int i = 0; i < bits; ++i) {
			s += (val & (1 << (bits - i - 1))) != 0 ? "1" : "0";
		}
		return s;
	}

	String getRomHeader() {

		String line = "--\n";
		line += "--\tpatmos_rom.vhd\n";
		line += "--\n";
		line += "--\tgeneric VHDL version of ROM\n";
		line += "--\n";
		line += "--\t\tDONT edit this file!\n";
		line += "--\t\tgenerated by " + this.getClass().getName() + "\n";
		line += "--\n";
		line += "\n";
		line += "library ieee;\n";
		line += "use ieee.std_logic_1164.all;\n";
		line += "\n";
		line += "entity patmos_rom is\n";
		// line +=
		// "generic (width : integer; addr_width : integer);\t-- for compatibility\n";
		line += "port (\n";
		line += "    address : in std_logic_vector(" + (ADDRBITS - 1)
				+ " downto 0);\n";
		line += "    q : out std_logic_vector(" + (DATABITS - 1)
				+ " downto 0)\n";
		line += ");\n";
		line += "end patmos_rom;\n";
		line += "\n";
		line += "architecture rtl of patmos_rom is\n";
		line += "\n";
		line += "begin\n";
		line += "\n";
		line += "process(address) begin\n";
		line += "\n";
		line += "case address is\n";

		return line;
	}

	String getRomFeet() {

		String line = "\n";
		line += "    when others => q <= \"" + bin(0, DATABITS) + "\";\n";
		line += "end case;\n";
		line += "end process;\n";
		line += "\n";
		line += "end rtl;\n";

		return line;
	}

	public void dump(List list) {

		try {

			FileWriter romvhd = new FileWriter(dstDir + "patmos_rom.vhd");
			romvhd.write(getRomHeader());

			Object o[] = list.toArray();
			for (int i = 0; i < o.length; ++i) {
				int val = ((Integer) o[i]).intValue();
				romvhd.write("    when \"" + bin(i, ADDRBITS) + "\" => q <= \""
						+ bin(val, DATABITS) + "\";");
//				romvhd.write(" -- " + inraw.readLine() + "\n");
				romvhd.write("\n");

			}

			romvhd.write(getRomFeet());
			romvhd.close();

			// PrintStream rom_mem = new PrintStream(new FileOutputStream(dstDir
			// + "mem_rom.dat"));
			// for (int i=0; i<ROM_LEN; ++i) {
			// rom_mem.println(romData[i]+" ");
			// }
			// rom_mem.close();

		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(-1);
		}
	}

	private boolean processOptions(String clist[]) {
		boolean success = true;

		for (int i = 0; i < clist.length; i++) {
			if (clist[i].equals("-s")) {
				srcDir = clist[++i];
			} else if (clist[i].equals("-d")) {
				dstDir = clist[++i];
			} else {
				fname = clist[i];
			}
		}

		return success;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		if (args.length < 1) {
			System.out
					.println("usage: java Jopa [-s srcDir] [-d dstDir] filename");
			System.exit(-1);
		}
		LerosAsm la = new LerosAsm(args);

		InputStream istr = new FileInputStream(la.srcDir + la.fname);
		ANTLRInputStream input = new ANTLRInputStream(istr);
		LerosLexer lexer = new LerosLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		LerosParser parser = new LerosParser(tokens);
		parser.pass1();
		// parser.dump();
		parser.reset();
		List code = parser.pass2();
		System.out.println(code);

		la.dump(code);
	}

}