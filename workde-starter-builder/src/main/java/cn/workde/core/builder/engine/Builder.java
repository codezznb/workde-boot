package cn.workde.core.builder.engine;

import cn.workde.core.base.properties.WorkdeProperties;
import cn.workde.core.base.utils.SpringUtils;
import cn.workde.core.boot.launch.WorkdeApplication;
import lombok.Data;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author zhujingang
 * @date 2019/9/17 10:29 PM
 */
@Data
public class Builder {

	private String projectPath;

	private String modulePath;

	public Builder() {

		this.projectPath = Builder.class.getResource("/").getPath().split("!")[0];
		int index = this.projectPath.indexOf("/target/classes/");
		this.projectPath = this.getProjectPath().substring(0, index);
		this.modulePath = new File(projectPath, "engine").getAbsolutePath();

		File moduleFolder = getModuleFolder();
		if(!moduleFolder.exists()) moduleFolder.mkdir();

		File systemFolder = getSystemFolder();
		if(!systemFolder.exists()) systemFolder.mkdir();
	}

	public File getModuleFolder() {
		return new File(modulePath);
	}

	public File getSystemFolder() {
		return new File(projectPath, "system");
	}

	public File getControlFile() {
		return new File(getSystemFolder(), "controls.json");
	}

	private static Builder instance;

	public static Builder getInstance() {
		if(instance == null) instance = new Builder();
		return instance;
	}
}
