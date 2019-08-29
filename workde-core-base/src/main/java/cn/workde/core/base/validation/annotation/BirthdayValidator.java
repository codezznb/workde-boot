package cn.workde.core.base.validation.annotation;

import cn.hutool.core.lang.Validator;
import cn.workde.core.base.utils.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author zhujingang
 * @date 2019/8/29 9:15 PM
 */
public class BirthdayValidator implements ConstraintValidator<Birthday, String> {

	private boolean notNull;

	@Override
	public void initialize(Birthday constraintAnnotation) {
		this.notNull = constraintAnnotation.notNull();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (StringUtils.isNotBlank(value)) {
			return Validator.isBirthday(value);
		}

		if (notNull) {
			return false;
		}

		return true;
	}

}
