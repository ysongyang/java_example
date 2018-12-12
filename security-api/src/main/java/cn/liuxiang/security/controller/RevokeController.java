package cn.liuxiang.security.controller;

import cn.liuxiang.security.util.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Auther: ysongyang (zz1.com.cn)
 * @Date: 2018/12/6 0006 15:13
 * @Description: 消费撤销接口
 */
@RestController
@RequestMapping("revoke")
public class RevokeController {

    /**
     * 请求<br>
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/request")
    public Result ApplyCode(HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setContentType("application/json;charset=utf-8");// 指定返回的格式为JSON格式
        response.setCharacterEncoding("UTF-8");// setContentType与setCharacterEncoding的顺序不能调换，否则还是无法解决中文乱码的问题

        String platformId = request.getParameter("platformId"); //平台ID
        String orderId = request.getParameter("orderId"); //撤销订单号
        String originalOrderId = request.getParameter("originalOrderId"); //原交易订单号
        String mchntCd = request.getParameter("mchntCd"); //商户号
        String termId = request.getParameter("termId"); //设备编号
        String tranTp = request.getParameter("tranTp"); //交易类型
        String termType = request.getParameter("termType"); //设备类型
        String txnTime = request.getParameter("txnTime"); //订单发送时间
        String tranAmout = request.getParameter("tranAmout"); //交易金额
        String tranCurCode = request.getParameter("tranCurCode"); //交易币种

        String cpppsUrl = request.getParameter("cpppsUrl"); //申码请求的地址

        Map<String, String> reqMap = new TreeMap<String, String>();
        reqMap.put("platformId", platformId);//平台Id
        reqMap.put("orderId", orderId);//商户订单号
        reqMap.put("mchntCd", mchntCd);//商户号
        reqMap.put("termId", termId);//设备编号
        reqMap.put("tranTp", tranTp);//交易类型
        reqMap.put("originalOrderId", originalOrderId);
        reqMap.put("txnTime", txnTime);
        reqMap.put("tranAmout", tranAmout);
        reqMap.put("tranCurCode", tranCurCode);
        reqMap.put("termType", termType);


        //将map的key按照a-z 0-9的顺序排序后组成 &拼接的字符串形式
        String mapStr = BeanUtil.map2kvStr(reqMap);

        String sign = SecurityUtils.sign(mapStr);
        if (sign == null) {
            return ResultUtil.error(1, "加签失败");
        }
        mapStr = mapStr + "&signature=" + sign;

        String rspStr = HttpRequest.sendPost(cpppsUrl, mapStr);
        Map<String, String> respMap = new TreeMap<String, String>();
        //K-V字符串转Map
        respMap = BeanUtil.kvStr2Map(rspStr);
        String signature = respMap.get("signature");
        respMap.remove("signature");
        //将map的key按照a-z 0-9的顺序排序后组成 &拼接的字符串形式
        String rspmapStr = BeanUtil.map2kvStr(respMap);
        boolean isok = SecurityUtils.checkSign(rspmapStr, signature);
        if (!isok) {
            return ResultUtil.error(1, "验签失败", rspStr);
        }
        return ResultUtil.success(rspStr);
    }

}
