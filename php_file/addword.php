<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php'; //数据库链接
$json_string = file_get_contents('php://input');
$body = json_decode($json_string,true);

//word_table
$word_group = $body['word_group'];
$C_meaning = $body['C_meaning'];
$page = $body['page'];
mysqli_query($conn,'INSERT word_table(word_group,C_meaning,page,collect) VALUES("'.$word_group.'","'.$C_meaning.'","'.$page.'",0)');
$word_id = mysqli_insert_id($conn);

//example_table
$translate = $body['translate'];
for($i=0;$i<count($translate);$i++){
	$word_meaning = $translate[$i]['word_meaning'];
	$C_translate = $translate[$i]['C_translate'];
	$E_sentence = $translate[$i]['E_sentence'];
	mysqli_query($conn,'INSERT example_table(word_meaning,E_sentence,C_translate,wid) VALUES("'.$word_meaning.'","'.$E_sentence.'","'.$C_translate.'",'.$word_id.')');
}

//recite_table
mysqli_query($conn,'INSERT recite_table(wid,correct_times,error_times,prof_flag) VALUES('.$word_id.',0,0,0)');

//关闭连接
mysqli_close($conn);

?>

