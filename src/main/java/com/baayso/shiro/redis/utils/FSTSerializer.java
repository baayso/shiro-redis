package com.baayso.shiro.redis.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.ruedigermoeller.serialization.FSTObjectInput;
import de.ruedigermoeller.serialization.FSTObjectOutput;

/**
 * 使用FST实现的序列化。
 * 
 * @author ChenFangjie(2015年3月18日 下午2:55:41)
 * 
 * @since 1.0.0
 * 
 * @version 1.0.0
 *
 */
public class FSTSerializer implements Serializer {

    private static Logger logger = LoggerFactory.getLogger(FSTSerializer.class);

    public static final String FST_SERIALIZER = "fst";

    @Override
    public String name() {
        return FST_SERIALIZER;
    }

    @Override
    public byte[] serialize(Object obj) {
        if (null == obj) {
            return new byte[0];
        }
        else if (!(obj instanceof Serializable)) {
            throw new IllegalArgumentException("[" + obj.getClass().getName() + "] does not implement java.io.Serializable interface.");
        }

        byte[] result = null;
        ByteArrayOutputStream bytesOutputStream = new ByteArrayOutputStream(128);
        FSTObjectOutput fstOutputStream = null;

        try {
            fstOutputStream = new FSTObjectOutput(bytesOutputStream);
            fstOutputStream.writeObject(obj);
            result = bytesOutputStream.toByteArray();
        }
        catch (IOException e) {
            logger.error("Failed to serialize.", e);
        }
        catch (Exception e) {
            logger.error("Failed to serialize.", e);
        }
        finally {
            if (null != fstOutputStream) {
                try {
                    fstOutputStream.close();
                }
                catch (IOException e) {
                }
            }
        }

        return result;
    }

    @Override
    public Object deserialize(byte[] bytes) {
        if (null == bytes || 0 == bytes.length) {
            return null;
        }

        Object result = null;
        FSTObjectInput fstObjectInput = null;

        try {
            fstObjectInput = new FSTObjectInput(new ByteArrayInputStream(bytes));
            result = fstObjectInput.readObject();
        }
        catch (ClassNotFoundException e) {
            logger.error("Class of a serialized object cannot be found.", e);
        }
        catch (IOException e) {
            logger.error("Failed to deserialize.", e);
        }
        catch (Exception e) {
            logger.error("Failed to deserialize.", e);
        }
        finally {
            if (null != fstObjectInput) {
                try {
                    fstObjectInput.close();
                }
                catch (IOException e) {
                }
            }
        }

        return result;
    }

}
