package com.hochoy.javabase.clone;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/7/15
 */
public class Teacher implements Cloneable {

    private String name;
    private Student student;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("name", name)
                .append("student", student.toString())
                .toString();
    }

    @Override
    public Teacher clone() throws CloneNotSupportedException {
        Teacher t2 = (Teacher)super.clone();
        t2.setStudent(t2.getStudent().clone());
        return t2;
    }
}
