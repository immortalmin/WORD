<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php'; //数据库链接
$result =mysqli_query($conn,"SELECT word_table.`wid`,word_table.`word_group`,word_table.`C_meaning`,word_table.`page`,word_table.`collect`,recite_table.`correct_times`,recite_table.`error_times`,recite_table.`prof_flag`,recite_table.`last_date` FROM word_table,recite_table WHERE word_table.`wid`=recite_table.`wid`");

while ($shopInfo = mysqli_fetch_array($result,MYSQLI_ASSOC)){ //返回查询结果到数组
	$output[]=$shopInfo;
}
if($output){
	echo json_encode( $output,JSON_UNESCAPED_UNICODE);
}else{
	echo json_encode([],JSON_UNESCAPED_UNICODE);
}

//4.释放内存中的查询结果
mysqli_free_result($result);
//5.关闭连接
mysqli_close($conn);
?>

