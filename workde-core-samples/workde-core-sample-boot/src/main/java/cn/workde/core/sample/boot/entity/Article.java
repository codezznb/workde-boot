package cn.workde.core.sample.boot.entity;

import cn.workde.core.admin.module.constant.Templets;
import cn.workde.core.admin.web.annotation.FieldDefine;
import cn.workde.core.tk.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Table;

/**
 * @author zhujingang
 * @date 2019/9/9 12:25 AM
 */
@Data
@Table(name="dt_article")
public class Article extends BaseEntity {

	/**
	 * 频道ID
	 */
	@ApiModelProperty("频道id(6:公告 7:政策 8:发文 14:热门服务)")
	@FieldDefine(title = "频道", listEnable = false)
	private Integer channelId;

	/**
	 * 类别ID
	 */
	@ApiModelProperty("类别id(3:公告 40:政策 29:发文 663:热门服务)")
	@FieldDefine(title = "类别", listEnable = false)
	private Integer categoryId;

	/**
	 * 调用别名
	 */
	private String callIndex;

	/**
	 * 标题
	 */
	@ApiModelProperty("标题")
	@FieldDefine(title = "标题", width = "15%")
	private String title;

	/**
	 * 外部链接
	 */
	private String linkUrl;

	/**
	 * 图片地址
	 */
	private String imgUrl;

	/**
	 * SEO标题
	 */
	private String seoTitle;

	/**
	 * SEO关健字
	 */
	private String seoKeywords;

	/**
	 * SEO描述
	 */
	private String seoDescription;

	/**
	 * TAG标签逗号分隔
	 */
	private String tags;

	/**
	 * 内容摘要
	 */
	@ApiModelProperty("内容摘要")
	@FieldDefine(title = "内容摘要", listEnable = false)
	private String zhaiyao;

	/**
	 * 详细内容
	 */
	@ApiModelProperty("详细内容")
	@FieldDefine(title = "详细内容", listEnable = false)
	private String content;

	/**
	 * 排序
	 */
	@ApiModelProperty("排序")
	@FieldDefine(title = "排序")
	private Integer sortId;

	/**
	 * 浏览次数
	 */
	@ApiModelProperty("浏览次数")
	@FieldDefine(title = "浏览次数")
	private Integer click;

	/**
	 * 状态0正常1未审核2锁定
	 */
	@ApiModelProperty("状态0正常1未审核2锁定")
	@FieldDefine(title = "状态")
	private Integer status;

	/**
	 * 是否允许评论
	 */
	@ApiModelProperty("是否允许评论")
	@FieldDefine(title = "是否允许评论", listTemplet = Templets.TEMPLET_SWITCH_STATE)
	private Integer isMsg;

	/**
	 * 是否置顶
	 */
	@ApiModelProperty("是否置顶")
	@FieldDefine(title = "是否置顶", listTemplet = Templets.TEMPLET_SWITCH_STATE)
	private Integer isTop;

	/**
	 * 是否推荐
	 */
	@ApiModelProperty("是否推荐")
	@FieldDefine(title = "是否推荐", listTemplet = Templets.TEMPLET_SWITCH_STATE)
	private Integer isRed;

	/**
	 * 是否热门
	 */
	@ApiModelProperty("是否热门")
	@FieldDefine(title = "是否热门", listTemplet = Templets.TEMPLET_SWITCH_STATE)
	private Integer isHot;

	/**
	 * 是否幻灯片
	 */
	@ApiModelProperty("是否幻灯片")
	private Integer isSlide;

	/**
	 * 是否管理员发布0不是1是
	 */
	@ApiModelProperty("是否管理员发布0不是1是")
	private Integer isSys;

	/**
	 * 用户名
	 */
	@ApiModelProperty("用户名")
	@FieldDefine(title = "用户名", listEnable = false)
	private String userName;

	private Integer userId;

	/**
	 * 所属地区省市县
	 */
	@ApiModelProperty("所属地区省市县")
	private String area;

	private Integer roleType;

	@ApiModelProperty("省")
	private String province;

	@ApiModelProperty("市")
	private String city;

	@ApiModelProperty("区")
	private String county;

	/**
	 * 乡镇街道
	 */
	@ApiModelProperty("乡镇街道")
	private String township;


}
