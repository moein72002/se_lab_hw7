package MiniJava.codeGenerator;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by mohammad hosein on 6/28/2015.
 */

@Getter
@Setter
public class Address {
    private int num;
    private TypeAddress Type;
    private varType varType;

    public Address(int num, varType varType, TypeAddress Type) {
        setNum(num);
        setType(Type);
        setVarType(varType);
    }

    public Address(int num, varType varType) {
        setNum(num);
        setType(TypeAddress.Direct);
        setVarType(varType);
    }

    public String toString() {
        switch (getType()) {
        case Direct:
            return getNum() + "";
        case Indirect:
            return "@" + getNum();
        case Imidiate:
            return "#" + getNum();
        }
        return getNum() + "";
    }
}
