--
-- Created by IntelliJ IDEA.
-- User: 91946
-- Date: 2018/1/6
-- Time: 15:24
-- To change this template use File | Settings | File Templates.
--

local uri = ngx.var.uri
--local test1  = require("requiretest")
local web = require("social.router.router")
local request = require("social.common.request")
local _util = require "new_base.util"
local permission_context = ngx.var.path_uri --有权限的context.
local permission_no_context = ngx.var.path_uri_no_permission
local ssdb_dao = require("space.course_package.dao.CoursePackageSSDBDao")
local _PersonInfo = require("space.services.PersonAndOrgBaseInfoService")
local quote = ngx.quote_sql_str
local management_context = ngx.var.management_path_uri
local TableUtil = require("social.common.table")
local cjson = require "cjson"
local prefix = ngx.config.prefix()
local filename = os.date("os%Y%m%d", os.time())
local filepath = prefix .. "logs/space/"
local mylog = string.format(filepath .. "%s.log", filename)
local log = require("social.common.log4j"):new(mylog)
local service = require("space.salary.service.salary_service")
local TS = require "resty.TS"


local function demo1()
    log:debug(111111111111111111111111111111111)
    local temp = { er = 1, erw = 'weqe', eweq = 345 }
    --    local result= sqlsfunc(temp)
--    ngx.say(cjson.encode("{\"0\":\"职员\",\"1\":\"职员类型\",\"2\":\"职员编码\",\"3\":\"职员名称\",\"4\":\"工作性补贴\",\"5\":\"生活性补贴\",\"6\":\"职务工资\",\"7\":\"级别工资\",\"8\":\"绩效工资\",\"9\":\"岗位工资\",\"10\":\"薪阶工资\",\"11\":\"浮动工资\",\"12\":\"工龄工资\",\"13\":\"行业补贴\",\"14\":\"独生费\",\"15\":\"电话费\",\"16\":\"补发工资\",\"17\":\"应发合计\",\"18\":\"采暖费\",\"19\":\"医疗保险\",\"20\":\"住房公积\",\"21\":\"社保\",\"22\":\"所得税\",\"23\":\"扣款合计\",\"24\":\"其他扣资\",\"25\":\"实发合计\",\"26\":\"\",\"27\":\"\",\"end\":\"end\"}"))
end

local function findsalarybykinds()
    local org_id = request:getNumParam("org_id", true, true)
    local org_type = request:getNumParam("org_type", true, true)
    local year_month_id = request:getStrParam("year_month_id", false, false)
    local name = request:getStrParam("name", false, false)
    local page_size = request:getNumParam("page_size", true, true)
    local page_num = request:getNumParam("page_num", true, true)
    if not name then
        name = ""
    end
    if not year_month_id or year_month_id == "" then
        ngx.say(cjson.encode({ success = false, info = "请输入月份" }))
        return;
    end
    local month_index = string.gsub(year_month_id,"-","")
    year_month_id = month_index
    log:debug('name:', name)
    local result, totalpage, total_row, titles = service.findsalarybykinds(org_id, org_type, year_month_id, name, page_size, page_num)
    local totalpageindex = totalpage
    if totalpageindex < page_num then
        result, totalpage, total_row, titles = service.findsalarybykinds(org_id, org_type, year_month_id, name, page_size, totalpageindex)
    end


    if not result or result == false then
        ngx.say(cjson.encode({ success = false, info = "获取列表失败，或操作有误" }))
        return;
    end
    ngx.say(cjson.encode({ success = true, resultTitles = titles, result = result, totalpage = totalpage, total_row = total_row, page_num = page_num, page_size = page_size }))
    return;
end

local function findsalarybyperson()
    local org_id = request:getNumParam("org_id", true, true)
    local org_type = request:getNumParam("org_type", true, true)
    local person_id = request:getNumParam("person_id", true, true)
    local year_month_id = request:getStrParam("year_month_id", true, true)
    local month_index = string.gsub(year_month_id,"-","")

    local param = {org_id = org_id,org_type=org_type,person_id = person_id}
    local result,titles  = service.findsalarybyperson(param,month_index)
    local empt = {}
    if  result ==false then
        ngx.say(cjson.encode({ success = true, resultTitles = empt,result=empt,info = "获取列表失败，或操作有误" }))
        return;
    end
    log:debug(result)
--    log:debug(TableUtil:length(result[1]['SALARY_DETAILS']))
    log:debug(result[1]['SALARY_DETAILS'])
    log:debug(type(result[1]['SALARY_DETAILS']))
    local year_month_index = result[1]["YEAR_MONTH_ID"]
--以下是在不熟练使用cjson的情况下，获取json字符串最后一个key值
    local a =0
    for w in string.gmatch(result[1]['SALARY_DETAILS'], ":") do  --匹配最长连续且只含字母的字符串
--        print(w)
        a=a+1
    end
    local index = a-1;
--    index = TableUtil:length(unjson)
--以上是在不熟练使用cjson的情况下，获取json字符串最后一个key值

    ngx.say(cjson.encode({success = true,resultTitles = titles,result = result,maxindex = index,year_month_index=year_month_index}))
    return;
end

local urls = {
    GET = {
        permission_no_context .. '/demo1', demo1,
        permission_no_context .. '/findsalarybykinds', findsalarybykinds,
        permission_no_context .. '/findsalarybyperson', findsalarybyperson,
    },
    POST = {--        management_context .. '/delete_boutique_lead$', deleteBoutiqueLead, --删除精品导学.
        --        management_context..'/addClass',addClass,
    }
}
local app = web.application(urls, nil)
app:start()