//test
//@author lh
//@category test
//@keybinding
//@menupath
//@toolbar

import ghidra.app.script.GhidraScript;
import ghidra.program.model.address.AddressSet;
import ghidra.program.model.listing.Function;
import ghidra.program.model.listing.FunctionIterator;
import ghidra.program.model.listing.Instruction;
import ghidra.program.model.listing.InstructionIterator;
import ghidra.program.model.listing.Listing;

public class test extends GhidraScript {

	@Override
	protected void run() throws Exception {
		//TODO: Add script code here
		println("hello, ghidra! this is a test");
		//currentProgram.
		AddressSet set = new AddressSet();
		Listing listing = currentProgram.getListing();

		InstructionIterator initer = listing.getInstructions(currentProgram.getMemory(), true);
		
		String opcodeStr;
		int oprandsNum;
		while (initer.hasNext() && !monitor.isCancelled()) {
			Instruction instruct = initer.next();
			set.addRange(instruct.getMinAddress(), instruct.getMaxAddress());
			opcodeStr = instruct.getMnemonicString();
			oprandsNum = instruct.getNumOperands();
			
			if (opcodeStr.equals("ldr") || opcodeStr.equals("adrp") || opcodeStr.equals("adr")) {
				printf("instruction addr: " + instruct.getAddress().toString() + "  opcode: " + opcodeStr + " oprandsNum:" + oprandsNum);
				for (int i = 0; i < oprandsNum; i++) {
					//
					printf("%d ", instruct.getOperandType(i));
					printf(instruct.getDefaultOperandRepresentation(i));
				}
			}

			
		}
		FunctionIterator iter = listing.getFunctions(true);
		while (iter.hasNext() && !monitor.isCancelled()) {
			Function f = iter.next();
			//printf("function: " + f.getName());
			set.delete(f.getBody());
		}

	}
}
