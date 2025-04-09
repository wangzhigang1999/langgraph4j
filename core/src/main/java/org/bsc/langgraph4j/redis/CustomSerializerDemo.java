package org.bsc.langgraph4j.redis;

import java.io.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CustomSerializerDemo {
    public static void main(String[] args) {
        // 创建要序列化的对象
        Employee employee = new Employee("李四", "工程师", 50000);
        
        // 创建自定义序列化器
        EmployeeSerializer serializer = new EmployeeSerializer();
        
        try {
            // 序列化
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(baos);
            serializer.write(employee, out);
            out.close();
            
            byte[] serializedData = baos.toByteArray();
            System.out.println("序列化完成，数据大小: " + serializedData.length + " 字节");
            
            // 反序列化
            ByteArrayInputStream bais = new ByteArrayInputStream(serializedData);
            ObjectInputStream in = new ObjectInputStream(bais);
            Employee deserializedEmployee = serializer.read(in);
            in.close();
            
            System.out.println("反序列化完成，对象内容: " + deserializedEmployee);
            
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

class Employee {
    private final String name;
    private final String position;
    private final int salary;
    
    public Employee(String name, String position, int salary) {
        this.name = name;
        this.position = position;
        this.salary = salary;
    }
    
    @Override
    public String toString() {
        return "Employee [name=" + name + ", position=" + position + ", salary=" + salary + "]";
    }
}

class EmployeeSerializer {
    private final Gson gson;
    
    public EmployeeSerializer() {
        this.gson = new GsonBuilder().serializeNulls().create();
    }
    
    public void write(Employee employee, ObjectOutput out) throws IOException {
        // 将对象转为JSON
        String json = gson.toJson(employee);
        // 写入JSON字符串长度
        out.writeInt(json.length());
        // 写入JSON字符串
        out.writeUTF(json);
    }
    
    public Employee read(ObjectInput in) throws IOException, ClassNotFoundException {
        // 读取JSON字符串长度
        int length = in.readInt();
        // 读取JSON字符串
        String json = in.readUTF();
        // 将JSON转回对象
        return gson.fromJson(json, Employee.class);
    }
}
