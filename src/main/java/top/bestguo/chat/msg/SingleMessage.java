package top.bestguo.chat.msg;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SingleMessage<T> extends Message {

    T data;

}
