package Symbol.LLVMToken;

import Symbol.ExpInfo;

//一个立即数或者一个寄存器编号
public class LLVMToken {
    private LLVMTokenType llvmTokenType;
    private int imm;
    private int reg;
    public LLVMToken(LLVMTokenType llvmTokenType, int num) {
        this.llvmTokenType = llvmTokenType;
        if (llvmTokenType.equals(LLVMTokenType.IMM) == true) {
            imm = num;
        } else {
            reg = num;
        }
    }

    public LLVMToken(int imm) {
        this.imm = imm;
        this.llvmTokenType = LLVMTokenType.IMM;
    }

    public LLVMToken(ExpInfo expInfo) {
        this.llvmTokenType = LLVMTokenType.REG;
        this.reg = expInfo.regIndex;
    }

    @Override
    public String toString() {
        if (llvmTokenType.equals(LLVMTokenType.IMM) == true) {
            return imm + "";
        } else {
            return "%" + reg;
        }
    }
}
