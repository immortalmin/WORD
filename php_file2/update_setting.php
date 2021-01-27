<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php'; //数据库链接
$json_string = file_get_contents('php://input');
$body = json_decode($json_string,true);
$uid = $body['uid'];
//$recite_num = $body['recite_num'];
//$recite_scope = $body['recite_scope'];
//mysqli_query($conn,'UPDATE setting SET recite_num='.$recite_num.',recite_scope='.$recite_scope.' WHERE uid='.$uid);

$isFirst = true;
$queryStr = '';
if(isset($body['recite_num'])){
	if(!$isFirst) $queryStr = $queryStr.',';
	$isFirst = false;
	$queryStr = $queryStr.'recite_num='.$body['recite_num'];
}
if(isset($body['recite_scope'])){
	if(!$isFirst) $queryStr = $queryStr.',';
	$isFirst = false;
	$queryStr = $queryStr.'recite_scope='.$body['recite_scope'];
}
mysqli_query($conn,'UPDATE setting set '.$queryStr.' WHERE uid='.$uid);


//5.关闭连接
mysqli_close($conn);
?>

