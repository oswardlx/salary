--
-- Created by IntelliJ IDEA.
-- User: 91946
-- Date: 2018/1/6
-- Time: 16:28
-- To change this template use File | Settings | File Templates.
--

local baseDao = require("social.dao.CommonBaseDao")
local DBUtil = require "social.common.mysqlutil";
local prefix = ngx.config.prefix()
local filename = os.date("os%Y%m%d", os.time())
local filepath = prefix .. "logs/space/"
local mylog = string.format(filepath.."%s.log", filename)
local log = require("social.common.log4j"):new(mylog)
local TableUtil = require("social.common.table")
local quote = ngx.quote_sql_str
local ssdbutil = require "social.common.ssdbutil"

local _M = {}
function _M.querySql(sql)
    local db = DBUtil:getDb();
    local result = db:query(sql)
    if not result then
        return false
    end
    return result
end
function _M.sqlsfunc(param)
    local column = {}
    for key, var in pairs(param) do
        if param[key] and tostring(param[key]) ~= "-1" then
            table.insert(column, key .. "=" .. quote(var))
        end
    end
    local str ="and %s "
    local sql = str:format(table.concat(column," and "))
    return sql
end

local function computePage(count, page_size, page_num)
    local _page_num = page_num;
    local Page = math.floor((count + page_size - 1) / page_size)
    if Page > 0 and page_num > Page then
        page_num = Page
    end
    local offset = page_size * page_num - page_size
    if _page_num > Page then
        return Page, 10000000
    end
    return Page, offset
end


local function findsalarybykindsCount( addsql2, addsql3,addsql4,addsql5)
    local sql = "select COUNT(DISTINCT id) as Row from T_SOCIAL_SALARY where 1=1  "..addsql3..addsql2..addsql4..addsql5
    log:debug(sql)
    local result =_M.querySql(sql)
    if result and result[1] then
        return result[1]['Row']
    end
    return 0;
end

local function findsalarybykindsList(addsql3, addsql2, page_size, offset,addsql4,addsql5)
    local sql = "SELECT * FROM t_social_salary where 1=1 "..addsql5..addsql4..addsql3..addsql2.." GROUP BY id order by create_time DESC limit "..offset..","..page_size..";"
    log:debug(sql)
    local result = _M.querySql(sql)
    return result
end


function _M.findsalarybykinds(org_id,org_type,year_month_id,name,page_size,page_num)
    log:debug(1111111111111111111)
    log:debug(name)
    local addsql2 = " and org_id = "..org_id
    local addsql3 = " and org_type = "..org_type
    local addsql4 = " and year_month_id = "..year_month_id
    local addsql5 = " and name like '%" .. name .. "%' "
    local count = findsalarybykindsCount( addsql2, addsql3,addsql4,addsql5);
    local list = {}
    local _page = 0
    local offset = 0
    if count and tonumber(count) > 0 then
        _page, offset = computePage(count, page_size, page_num);
        list = findsalarybykindsList(addsql3, addsql2, page_size, offset,addsql4,addsql5);
    end
    return list, _page, #list
end
function _M.findtitalbykinds(org_id,org_type,year_month_id)
    local sql = "Select * FROM t_social_salary_titles where org_id = "..org_id.." and  org_type = "..org_type.." and year_month_id = "..year_month_id
    local result = _M.querySql(sql)
    return result
end

function _M.findsalarybyperson(param,month_index)
    local addsql1  =_M.sqlsfunc(param,month_index)
    local sql = "Select * from t_social_salary where 1=1 "..addsql1.." and year_month_id = "..month_index
    log:debug(sql)
    local result = _M.querySql(sql)
    log:debug(result)
--    if TableUtil:length(result)<1 then
----        sql = "Select * from t_social_salary where 1=1 "..addsql1.."order by year_month_id DESC limit 1"
----        log:debug(sql)
----        local result1= _M.querySql(sql)
--        return false
--    end
    return result
end

return baseDao:inherit(_M):init()