<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php'; //数据库链接
$mount = $_GET["mount"];
$result =mysql_query("SELECT * FROM recite_words WHERE prof_flag=0 ORDER BY correct_times LIMIT ".$mount);
/*
 mysql_fetch_array:从结果集中取得一行作为关联数组，或数字数组，或二者兼有。
 除了将数据以数字索引方式储存在数组中之外
 ，还可以将数据作为关联索引储存，用字段名作为键名。 
 也就是说他得到的结果像数组一样，可以用key或者索引来取值，所以 
$shopInfo["字段名"]或者 $shopInfo[字段的序号]都能得到相应的值
 */
while ($shopInfo = mysql_fetch_array($result,MYSQL_ASSOC)){ //返回查询结果到数组
	/*循环一次获取一条结果集的记录并保存在$shopInfo中*/
	//将一个关联数组作为一个数组元素保存到数组 $output[]中
	 $output[]=$shopInfo;
}
echo json_encode( $output,JSON_UNESCAPED_UNICODE)  ;
//4.释放内存中的查询结果
mysql_free_result($result);
//5.关闭连接
mysql_close($conn);
?>

