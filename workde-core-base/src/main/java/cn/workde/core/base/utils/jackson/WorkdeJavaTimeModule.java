package cn.workde.core.base.utils.jackson;

import cn.workde.core.base.utils.DateUtils;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * java 8 时间默认序列化
 * @author zhujingang
 * @date 2019/9/1 10:45 AM
 */
public class WorkdeJavaTimeModule extends SimpleModule {

	public WorkdeJavaTimeModule() {
		super(PackageVersion.VERSION);
		this.addDeserializer(LocalDateTime.class,  new LocalDateTimeDeserializer(DateUtils.y_M_d_H_m_s));
		this.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateUtils.y_M_d));
		this.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateUtils.H_m_s));
		this.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateUtils.y_M_d_H_m_s));
		this.addSerializer(LocalDate.class, new LocalDateSerializer(DateUtils.y_M_d));
		this.addSerializer(LocalTime.class, new LocalTimeSerializer(DateUtils.H_m_s));
	}
}
