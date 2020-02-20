<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php'; //数据库链接
//$id = $_GET["id"];
$result =mysqli_query($conn,"SELECT COUNT(*)sum,SUM(CASE WHEN prof_flag=1 THEN 1 ELSE 0 END)prof_count FROM recite_table;");
/*
 mysql_fetch_array:从结果集中取得一行作为关联数组，或数字数组，或二者兼有。
 除了将数据以数字索引方式储存在数组中之外
 ，还可以将数据作为关联索引储存，用字段名作为键名。 
 也就是说他得到的结果像数组一样，可以用key或者索引来取值，所以 
$shopInfo["字段名"]或者 $shopInfo[字段的序号]都能得到相应的值
 */
while ($shopInfo = mysqli_fetch_array($result,MYSQLI_ASSOC)){ //返回查询结果到数组
	/*循环一次获取一条结果集的记录并保存在$shopInfo中*/
	//将一个关联数组作为一个数组元素保存到数组 $output[]中
	 $output[]=$shopInfo;
}
echo json_encode( $output,JSON_UNESCAPED_UNICODE)  ;
//4.释放内存中的查询结果
mysqli_free_result($result);
//5.关闭连接
mysqli_close($conn);
?>

