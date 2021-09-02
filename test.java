//test
//@author lh
//@category test
//@keybinding
//@menupath
//@toolbar

import static org.junit.Assert.assertNotNull;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.python.modules.itertools.ifilter;

import ghidra.app.script.GhidraScript;
import ghidra.program.model.address.AddressSet;
import ghidra.program.model.address.Address;
import ghidra.program.model.listing.Function;
import ghidra.program.model.listing.FunctionIterator;
import ghidra.program.model.listing.Instruction;
import ghidra.program.model.listing.InstructionIterator;
import ghidra.program.model.listing.Listing;
import ghidra.program.model.symbol.*;
import jnr.ffi.Struct.int16_t;

public class test extends GhidraScript {

	@Override
	protected void run() throws Exception {
		//TODO: Add script code here

		// 1. recognize all ld/st instructions
		// 		
		// 2. recognize the reference type of ld/st instructions
		// 3. determine how to deal with a specific ld/st instruction
		// 4. if do NOT need change, just output the getDefaultOperandRepresentation
		// 	  if there is a branch reference to this instruction, need output a label for this instruction
		//    Note: 
		// 5. if do     need change,
		// 6. need know more about instruction references ...
		//
		// 7. recognize all branch instructions
		// 8. branch to local Label
		// 9. branch to local functions
		// 10.branch to external functions

		// TODO:
		// adrp x1 0x8002a000;  add x1 x1 #0x150; ldr x6 [x1, #0x10]

		printf("hello, ghidra! this is a test");
		//currentProgram.
		AddressSet set = new AddressSet();
		Listing listing = currentProgram.getListing();
		
		InstructionIterator initer = listing.getInstructions(currentProgram.getMemory(), true);

		FunctionIterator iter = listing.getFunctions(true);
		boolean find = false;
		while (iter.hasNext() && !monitor.isCancelled()) {
			Function f = iter.next();
			if (f.getName().equals("main")) {
				initer = listing.getInstructions(f.getBody(), true);
				find = true;
			}
		}
		
		if (!find) {
			printf("Cannot find main function, over.");
			return;
		}
		
		
		
		String opcodeStr;
		int oprandsNum;
		while (initer.hasNext() && !monitor.isCancelled()) {
			Instruction instruct = initer.next();
			set.addRange(instruct.getMinAddress(), instruct.getMaxAddress());
			opcodeStr = instruct.getMnemonicString();
			oprandsNum = instruct.getNumOperands();
			
			// construct the oprand string, that is useful for generating assembly file
			String oprandStr = " ";
			for (int i = 0; i < oprandsNum; i++) {
				oprandStr += instruct.getDefaultOperandRepresentation(i);
				oprandStr += " ";
			}
			printf("instruction addr: " + instruct.getAddress().toString() + "  " + opcodeStr + oprandStr + " oprandsNum:" + oprandsNum);


			// Q: what is MnemonicReference ?
			Reference [] mRef = instruct.getMnemonicReferences();
			if (mRef.length > 0) {
				for (int i = 0; i < mRef.length; i++) {
					if (mRef[i].isMnemonicReference()) {
						println("isMnemonicReference == true");
						break;
					}
				}
			}

			if (opcodeStr.equals("ldr") || opcodeStr.equals("adrp") || opcodeStr.equals("adr")) {
				for (int i = 0; i < oprandsNum; i++) {
					//printf(instruct.getDefaultOperandRepresentation(i));

				}
			}

			
			// this is an example for traversing all references FROM this instruction
			// getReferencesFrom function only cares about the SOURCE oprand of the instruction
			// Note: Not every instructing has References!
			Reference [] fromRef = instruct.getReferencesFrom();
			if (fromRef.length > 0) {
				String output = "	";
				for (int i = 0; i < fromRef.length; i++) {
					int opIdx = fromRef[i].getOperandIndex();
					String opStr = instruct.getDefaultOperandRepresentation(opIdx);
					output += opStr;
					output += " ";
					
					if (fromRef[i].isOperandReference()) {  // seems always true
						output += "isOperandReference, ";
					}
					if (fromRef[i].isMemoryReference()) {   // local memory ?
						output += "isMemoryReference, ";
					} 
					if (fromRef[i].isExternalReference()) { // imported reference
						output += "isExternalReference, ";
					}
					if (fromRef[i].isOffsetReference()) {   // ? 
						output += "isOffsetReference, ";
					}
					if (fromRef[i].isRegisterReference()) {
						output += "isRegisterReference, ";
					}
					if (fromRef[i].isShiftedReference()) {
						output += "isShiftedReference, ";
					}
					if (fromRef[i].isStackReference()) {
						output += "isStackReference, ";
					}
				}
				printf(output);
			}

			// similarly you can do similar things w.r.t. TO 
			// getReferencesIteratorTo function only cares about the DES oprand of the instruction
			// ReferenceIterator toRefIter = instruct.getReferenceIteratorTo();
			
		}
	}
}
