import model.ResponseMessage;

public class NumberGuessService {

    public static  ResponseMessage guessNumber(Integer number) {
       if(number < NumberConstant.INTEGER){
           return ResponseMessage.LOW;
       }else  if(number > NumberConstant.INTEGER){
           return  ResponseMessage.HIGH;
       }
       return  ResponseMessage.EQUAL;
    }
}
