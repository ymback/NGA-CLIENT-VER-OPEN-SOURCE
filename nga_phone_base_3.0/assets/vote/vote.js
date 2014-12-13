//投票显示=================
function vote(tid, x) {
    var sum = 0, vsum = 0, usum = 0, w = window, id = 'vote' + Math.random(), type = 'radio'

    s = x.split('~')
    if (s.length == 1)
        x = s
    else {
        var v = {}
        for (var i = 0; i < s.length; i += 2)
            v[s[i]] = s[i + 1]
        x = v
    }
    if (x.max_select > 1)
        type = 'checkbox'

    for (var k in x) {
        if (!parseInt(k, 10))
            continue;
        var y = x['_' + k].split(',')
        x[k] = [x[k], parseInt(y[0], 10), parseInt(y[1], 10)]
        usum = parseInt(y[2], 10)
        sum += x[k][1]
        if (y[1])
            vsum += x[k][2]
    }
    var name = x.type == 1 ? '投注' : '投票',nametitle = x.type == 1 ? '投注(输入框中只能输入数字哦)' : '投票', atv = true, txt = "<h4 class='silver'>" + nametitle + "</h4><table id='" + id + "'><tbody>"
    if (x.end != null) {
        if (x.end < Math.round(new Date().getTime() / 1000))
            atv = false;
    }
    for (var k in x) {
        if (!parseInt(k, 10))
            continue;
        txt += "<tr><td>" + x[k][0] + "</td>";
        if (atv) {
            if (x.type == 1)
                txt += "<td><input name='" + k + "' value='' title='如投注此项则在此填入投注数量' type='number' style='width:2em'/></td>"
            else
                txt += "<td><input type='" + type + "' name='vote" + tid + "' value='" + k + "'/></td>"
        }
        if (!atv && x.type == 1 && !x.done) {
            txt += "<td><input type='" + type + "' name='vote" + tid + "' value='" + k + "'/></td>"
        }
        if (x.done) {
            if (k.indexOf(x.done) != -1)
                txt += "<td><b class='red'>胜出</b></td>"
            else
                txt += "<td></td>"
        }
        txt += "<td><b>" + x[k][1] + "人("+ ((sum) ? Math.round(x[k][1] / sum * 10000)/100 : 0) + "%)</b></td>"
        if (x.type == 1) {
            txt += "<td><b>投注" + x[k][2] + "("+((vsum) ? Math.round(x[k][2] / vsum * 10000)/100 : 0) + "%)</b></td>"
        }
        txt += '</tr>'
    }
    txt += "</tbody></table><div> "

    if (x.type == 1)
        txt += "投注<a href='javascript:void(0)' class='b' onclick='alert(\"10000铜币=100银币=1金币\")'>铜币</a> 最少" + x.min + " 最多" + x.max + " 最多投注" + x.max_select + "项"
    else
        txt += "最多选择" + x.max_select + "项"

    if (usum)
        txt += " 共计" + usum + "人";

    txt += " 共计" + sum + (x.type == 1 ? "次投注" : "票");

    if (x.type == 1)
        txt += " 共计投注" + vsum + "铜币";

    if (x.end)
        txt += " 结束时间 " + this.time2date(x.end, 'Y-m-d H:i')

    if (atv)
        txt += "<br/><button type='button' onclick='submit(" + tid + "," + x.type + "," + x.max_select + "," + x.min + "," + x.max + "," + x.end + ")'>" + name + "</button> "

    if (!atv && x.type == 1 && !x.done)
        txt += "<br/><button type='button' onclick='submitSettle(" + tid + ")'>结算</button> (勾选判定为\"赢\"的选项)"

    document.getElementById("votec").innerHTML = '<div>' + txt + '</div></div>'


}//fe


function time2date(t, f) {
    if (!t)
        return '';
    if (!this._time2date_date)
        this._time2date_date = new Date;
    var y = this._time2date_date;
    y.setTime(t * 1000);
    if (!f)
        f = 'Y-m-d H:i:s'
    var x = function(s) {
        s = String(s);
        if (s.length < 2)
            s = '0' + s;
        return s
    }
    f = f.replace(/([a-zA-Z])/g, function($0, $1) {
        switch ($1)
        {
            case 'Y':
                return y.getFullYear()
            case 'y':
                $1 = String(y.getFullYear())
                return $1.substr($1.length - 2)
            case 'm':
                return x(y.getMonth() + 1)
            case 'd':
                return x(y.getDate())
            case 'H':
                return x(y.getHours())
            case 'i':
                return x(y.getMinutes())
            case 's':
                return x(y.getSeconds())
        }
    })
    return f
}//fe
function submitSettle(tid) {
    if (!confirm("你确认选对了么"))
        return
    var x = document.getElementById("votec").getElementsByTagName("input"), y = []
    for (var i = 0; i < x.length; i++) {
        if (x[i].checked) {
            y.push(x[i].value)
        }
    }
    if (y.length) {
        window.ProxyBridge.postURL("__lib=vote&raw=3&lite=js&__act=settle&tid=" + tid + "&voteid=" + y.join(','));
    }
}//fe
function check(s){
var reg=/^[1-9]\d*$|^0$/;
return reg.test(s);
}
function submit(tid, type, max_select, min, max, end) {
    var x = document.getElementById("votec").getElementsByTagName("input"), y = [], c = 0
    for (var i = 0; i < x.length; i++) {
        if (type == 1) {
            if (x[i].value) {
                c++
                if (c > max_select){
					alert('不能投注超过' + max_select + '项')
                    return false;
					}
                var z = x[i].value
				if(!check(z)){
					document.getElementById("votec").getElementsByTagName("input")[i].value=""
					alert('输入的内容只能是首位非0的数字')
                    return false;
				}
                if (z < min){
					alert('超过最小值')
					return false;
				}
                if (z > max){
					alert('超过最大值')
					return false;
				}
                y.push(x[i].name)
                y.push(x[i].value)
            }
        }
        else {
            if (x[i].checked) {
                c++
                if (c > max_select){
					alert('不能投注超过' + max_select + '项')
					return false;
				}
                y.push(x[i].value)
            }
        }

    }
    if (y.length) {
        window.ProxyBridge.postURL("__lib=vote&raw=3&lite=js&__act=vote&tid=" + tid + "&voteid=" + y.join(','));
    }else{
		alert('至少选择或输入一个内容')
		return false;
	}
}//fe