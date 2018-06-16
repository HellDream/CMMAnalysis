package semantic;

/**
 * Created by 79300 on 2017/11/29.
 */
public class Util {
    public static Value plus(Value left, Value right){
        Value value = new Value();
        if(left.isString()&&right.isString()){
            value.setString(true);
            String leftStr = left.getValueString();
            String rightStr = right.getValueString();
            value.setValueString(leftStr+rightStr);
            return value;
        }else if(left.isInt()&&right.isInt()){
            value.setInt(true);
            value.setValueInt(left.getValueInt()+right.getValueInt());
        }else { //if(left.isDouble()||right.isDouble())
            value.setValueDouble(left.getOnlyValue()+right.getOnlyValue());
            value.setDouble(true);
        }
        return value;
    }
    public static Value subtract(Value left, Value right){
        Value value = new Value();
        if(left.isInt()&&right.isInt()){
            value.setInt(true);
            value.setValueInt(left.getValueInt()-right.getValueInt());
        }else { //if(left.isDouble()||right.isDouble())
            value.setDouble(true);
            value.setValueDouble(left.getOnlyValue()-right.getOnlyValue());
        }
        return value;
    }
    public static Value mul(Value left, Value right){
        Value value = new Value();
        if(left.isInt()&&right.isInt()){
            value.setInt(true);
            value.setValueInt(left.getValueInt()*right.getValueInt());
        }else { //if(left.isDouble()||right.isDouble())
            value.setDouble(true);
            value.setValueDouble(left.getOnlyValue()*right.getOnlyValue());
        }
        return value;
    }
    public static Value div(Value left, Value right){
        Value value = new Value();
        if(right.getOnlyValue()==0){
            /**报错*/
        }
        else if(left.isInt()&&right.isInt()){
            value.setInt(true);
            value.setValueInt(left.getValueInt()/right.getValueInt());
        }else { //if(left.isDouble()||right.isDouble())
            value.setDouble(true);
            value.setValueDouble(left.getOnlyValue()/right.getOnlyValue());
        }
        return value;
    }
    public static Value Equal(Value left, Value right){
        Value value = new Value();
        value.setBool(true);
        if(left.getOnlyValue()==right.getOnlyValue()){
            value.setBool(1);
        }else value.setBool(0);
        return value;
    }
    public static Value notEqual(Value left, Value right){
        Value value = new Value();
        value.setBool(true);
        if(left.getOnlyValue()!=right.getOnlyValue()){
            value.setBool(1);
        }else value.setBool(0);
        return value;
    }
    public static Value moreThan(Value left, Value right){
        Value value = new Value();
        value.setBool(true);
        if(left.getOnlyValue()>right.getOnlyValue()){
            value.setBool(1);
        }else value.setBool(0);
        return value;
    }
    public static Value lessThan(Value left, Value right){
        Value value = new Value();
        value.setBool(true);
        if(left.getOnlyValue()<right.getOnlyValue()){
            value.setBool(1);
        }else value.setBool(0);
        return value;
    }
    public static Value lessEq(Value left, Value right){
        Value value = new Value();
        value.setBool(true);
        if(left.getOnlyValue()<=right.getOnlyValue()){
            value.setBool(1);
        }else value.setBool(0);
        return value;
    }
    public static Value moreEq(Value left, Value right){
        Value value = new Value();
        value.setBool(true);
        if(left.getOnlyValue()>=right.getOnlyValue()){
            value.setBool(1);
        }else value.setBool(0);
        return value;
    }
}
