package cn.workde.core.base.validation.annotation;

import cn.hutool.core.lang.Validator;
import cn.workde.core.base.utils.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author zhujingang
 * @date 2019/8/29 9:17 PM
 */
public class PlateNumberValidator implements ConstraintValidator<PlateNumber, String> {

	private boolean notNull;

	@Override
	public void initialize(PlateNumber constraintAnnotation) {
		this.notNull = constraintAnnotation.notNull();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (StringUtils.isNotBlank(value)) {
			return Validator.isPlateNumber(value);
		}

		if (notNull) {
			return false;
		}

		return true;
	}
}
