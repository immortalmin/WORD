<?php
header('content-type:application/json;charset=utf8');
include './include/conn.php'; //数据库链接
$json_string = file_get_contents('php://input');
$body = json_decode($json_string,true);
//因为在忘记密码中，无法得知用户id，所以只能通过电话号码修改密码
$telephone = $body['telephone'];
$pwd = $body['pwd'];
mysqli_query($conn,'UPDATE user SET pwd="'.$pwd.'" WHERE telephone="'.$telephone.'"');
//返回更新后的数据
$result =mysqli_query($conn,'SELECT * FROM user WHERE telephone="'.$telephone.'"');

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

