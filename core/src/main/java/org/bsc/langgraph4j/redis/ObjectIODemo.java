package org.bsc.langgraph4j.redis;

import java.io.*;

public class ObjectIODemo {
    public static void main(String[] args) {
        // 创建一个要序列化的对象
        Person person = new Person("张三", 30);

        try {
            // 序列化对象到文件
            FileOutputStream fileOut = new FileOutputStream("person.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(person);  // 使用 ObjectOutput 的 writeObject 方法
            out.close();
            fileOut.close();
            System.out.println("对象已序列化到 person.ser");

            // 从文件反序列化对象
            FileInputStream fileIn = new FileInputStream("person.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Person deserializedPerson = (Person) in.readObject();  // 使用 ObjectInput 的 readObject 方法
            in.close();
            fileIn.close();

            System.out.println("反序列化的对象: " + deserializedPerson);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

// 必须实现 Serializable 接口才能被序列化
class Person implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String name;
    private final int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person [name=" + name + ", age=" + age + "]";
    }
}
