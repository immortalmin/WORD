<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php'; //数据库链接
$json_string = file_get_contents('php://input');
$body = json_decode($json_string,true);
$id = $body['id'];
$word_meaning = $body["word_meaning"];
$E_sentence = $body["E_sentence"];
$C_translate = $body["C_translate"];
mysqli_query($conn,'UPDATE example_table SET word_meaning="'.$word_meaning.'",E_sentence="'.$E_sentence.'",C_translate="'.$C_translate.'" WHERE eid='.$id);

echo "更新成功";
//5.关闭连接
mysqli_close($conn);
?>

