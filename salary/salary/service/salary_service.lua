--
-- Created by IntelliJ IDEA.
-- User: 91946
-- Date: 2018/1/6
-- Time: 16:21
-- To change this template use File | Settings | File Templates.
--

local baseService = require("social.service.CommonBaseService")
local dao = require("space.salary.dao.salary_dao")
local TableUtil = require("social.common.table")
local _util = require "new_base.util"
local cjson = require "cjson"
local prefix = ngx.config.prefix()
local filename = os.date("os%Y%m%d", os.time())
local filepath = prefix .. "logs/space/"
local mylog = string.format(filepath .. "%s.log", filename)
local log = require("social.common.log4j"):new(mylog)
local _PersonInfo = require("space.services.PersonAndOrgBaseInfoService")
local _M = {}

function _M.findsalarybykinds(org_id, org_type, year_month_id, name, page_size, page_num)
    local result, totalpage, total_row = dao.findsalarybykinds(org_id, org_type, year_month_id, name, page_size, page_num)
    local titles = dao.findtitalbykinds(org_id, org_type, year_month_id)
    return result, totalpage, total_row, titles
end

function _M.findsalarybyperson(param, month_index)

    local result = dao.findsalarybyperson(param, month_index)
    log:debug(result);
    if TableUtil:length(result) < 1 then
        --        sql = "Select * from t_social_salary where 1=1 "..addsql1.."order by year_month_id DESC limit 1"
        --        log:debug(sql)
        --        local result1= _M.querySql(sql)
        return false, ""
    end
    --    if result==false then
    --        return false,"查询出错或暂无信息"
    --    end
    log:debug(result)
    log:debug(result[1])
    log:debug(result[1]["YEAR_MONTH_ID"])
    local titles = dao.findtitalbykinds(result[1]["ORG_ID"], result[1]["ORG_TYPE"], result[1]["YEAR_MONTH_ID"])
    return result, titles
end


return baseService:inherit(_M):init()