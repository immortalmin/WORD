<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php'; //数据库链接
$json_string = file_get_contents('php://input');
$body = json_decode($json_string,true);

$wid = $body['wid'];
$uid = $body['uid'];
$dict_source = $body['dict_source'];
$translate = $body['translate'];
for($i=0;$i<count($translate);$i++){
	$word_meaning = $translate[$i]['word_meaning'];
	$C_translate = $translate[$i]['C_translate'];
	$E_sentence = $translate[$i]['E_sentence'];
	if(strcmp($dict_source,"0")==0){
		mysqli_query($conn,"INSERT example(word_meaning,E_sentence,C_translate,wid,source) VALUES(\"".$word_meaning."\",\"".$E_sentence."\",\"".$C_translate."\",".$wid.",".$uid.")");
	}else{
		mysqli_query($conn,"INSERT example(word_meaning,E_sentence,C_translate,kid,source) VALUES(\"".$word_meaning."\",\"".$E_sentence."\",\"".$C_translate."\",".$wid.",".$uid.")");
	}
}


if(strcmp($dict_source,"0")==0){
	$result = mysqli_query($conn,'SELECT eid,word_meaning AS "word_en",E_sentence,C_translate,user.`username` AS "source" FROM example,user WHERE example.`wid`="'.$wid.'" AND example.`source`=user.`uid` ORDER BY example.`wid`;');
}else{
	$result = mysqli_query($conn,'SELECT eid,word_meaning AS "word_en",E_sentence,C_translate,user.`username` AS "source" FROM example,user WHERE example.`kid`="'.$wid.'" AND example.`source`=user.`uid` ORDER BY example.`kid`;');	
}

//$result =mysqli_query($conn,"SELECT example.`eid`,example.`word_meaning`,example.`E_sentence`,example.`C_translate`,user.`username` 'source' FROM user,example WHERE example.`wid`=".$wid." AND example.`source`=user.`uid` order by example.`eid`");

$output = [];
while ($shopInfo = mysqli_fetch_array($result,MYSQLI_ASSOC)){ //返回查询结果到数组
        $output[]=$shopInfo;
}
if($output){
        echo json_encode( $output,JSON_UNESCAPED_UNICODE);
}
else{
        echo json_encode([],JSON_UNESCAPED_UNICODE);
}

//4.释放内存中的查询结果
mysqli_free_result($result);

//关闭连接
mysqli_close($conn);

?>

