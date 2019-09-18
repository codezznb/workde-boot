package cn.workde.core.builder.engine;

import lombok.Data;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;

/**
 * @author zhujingang
 * @date 2019/9/17 10:29 PM
 */
@Data
public class Builder {

	private String projectPath;

	private String modulePath;

	public Builder() {
		this.projectPath = new ApplicationHome(getClass()).getSource().getParentFile().getParentFile().getAbsolutePath();
		this.modulePath = new File(projectPath, "modules").getAbsolutePath();
		
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
