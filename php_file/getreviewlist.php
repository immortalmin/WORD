<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php'; //数据库链接
$json_string = file_get_contents('php://input');
$body = json_decode($json_string,true);
$last_date = $body['last_date'];
$result =mysqli_query($conn,'SELECT word_table.`wid`,word_group,C_meaning,page,collect,correct_times,error_times,prof_flag,last_date FROM word_table,recite_table WHERE word_table.`wid`=recite_table.`wid` AND last_date="'.$last_date.'"');

while ($shopInfo = mysqli_fetch_array($result,MYSQLI_ASSOC)){ //返回查询结果到数组
	$output[]=$shopInfo;
}
echo json_encode( $output,JSON_UNESCAPED_UNICODE);
//4.释放内存中的查询结果
mysqli_free_result($result);
//5.关闭连接
mysqli_close($conn);
?>

