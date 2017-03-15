package compiler.util;

public enum Register {
	 R1("R1"), R2("R2"), R3("R3"), R4("R4"), R5("R5"), R6("R6"), R7("R7"), R8("R8"), R9("R9"), R10("R10"), 
	 R11("R11") , R12("R12") , R13("R13") , R14("R14"), R15("R15") , R16("R16"), R17("R17"), R18("R18"), R19("R19"), 
	 R20("R1"), R21("R21"), R22("R22"), R23("R23"), R24("R24"), R25("R25"), R26("R26"), R27("R27"), R28("R28"), R29("R29"), R30("R30"), 
	 R31("31") , R32("R32") , R33("R33") , R34("R34"), R35("R35") , R36("R36"), R37("R37"), R38("R38"), R39("R39"),
	 SP("SP"), _SP("*SP");
	
	private String value;
	
	private static Register[] vals = values();

	Register(String value) {

       this.value = value;
   }

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String toString(){
		return value;
	}
	public Register next()
    {
        Register next = vals[(this.ordinal()+1) % vals.length];
        if (next == SP) {next = vals[(this.ordinal()+2) % vals.length];}
        if (next == _SP) {next = vals[(this.ordinal()+3) % vals.length];}
		return next;
    }
}
