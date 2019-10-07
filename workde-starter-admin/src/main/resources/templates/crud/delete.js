var $ = layui.jquery;
Wb.msg("删除成功");
$('a[href=${id}].layui-btn-gray').parents('tr').remove();
