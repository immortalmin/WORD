<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php'; //数据库链接
$json_string = file_get_contents('php://input');
$body = json_decode($json_string,true);

//word_table
$word_group = $body['word_group'];
$C_meaning = $body['C_meaning'];
$uid = $body['uid'];
mysqli_query($conn,"INSERT words(word_group,C_meaning,source) VALUES(\"".$word_group."\",\"".$C_meaning."\",\"".$uid."\")");
$wid = mysqli_insert_id($conn);

//example_table
$translate = $body['translate'];
for($i=0;$i<count($translate);$i++){
	$word_meaning = $translate[$i]['word_meaning'];
	$C_translate = $translate[$i]['C_translate'];
	$E_sentence = $translate[$i]['E_sentence'];
	mysqli_query($conn,"INSERT example(word_meaning,E_sentence,C_translate,wid,source) VALUES(\"".$word_meaning."\",\"".$E_sentence."\",\"".$C_translate."\",".$wid.",".$uid.")");
}

//recite_table
mysqli_query($conn,'INSERT INTO recite(wid,uid) SELECT '.$wid.',user.`uid` FROM user;');

//关闭连接
mysqli_close($conn);

?>

