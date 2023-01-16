import TimeEntryService from "@/apis/TimeEntryService";
import * as RegExp from "@/assets/js/regExp.js";

/**
 *  登录注册基本项验证
 */
export const LoginRegisterFormMixin = {
    methods: {
        validate(field, name) {
            if (field == null || field.trim() === "") {
                this.errors[name] = "该项不能为空";
            } else {
                switch (name) {
                    case "phoneNumber":
                        if (!RegExp.MOBILE_PATTERN.test(field)) {
                            this.errors[name] = "请填写合法手机号";
                        }
                        break;
                    case "code":
                        if (!RegExp.CODE_PATTERN.test(field)) {
                            this.errors[name] = "验证码格式错误";
                        }
                        break;
                    case "password":
                        if (!RegExp.PASSWORD_PATTERN.test(field)) {
                            this.errors[name] = "密码应为8到16位";
                        }
                        break;
                    case "confirmPassword":
                        if (field !== this.user.password) {
                            this.errors[name] = "两次密码不一致";
                        }
                }
            }
        },
    },
};

export const TimeEntryMixin = {
    data() {
        return {
            contextWeekRange: [],
            currWeekRange: [],
            timeEntriesWeek: [], //从api直接获取的一周entry数据
            timeEntriesWeeklyOutput: [], // 用来页面展示的entry
        };
    },
    methods: {
        // 获取一周entry
        // start 一周开始日期 2022-01-01
        // end一周结束日期
        async getAllTimeEntriesOfWeek(start, end) {
            const res = await TimeEntryService.list(
                this.user.id,
                start,
                end,
                this.user.timezone,
                this.user.showShortEntry
            );
            if (res.success) {
                this.timeEntriesWeek = res.content;
            } else {
                this.$toast.error("获取时间条目失败");
            }
        },
    },
    computed: {
        /*
         * 计算追踪小时数图标的xy数据
         * @return {dates: 日期数组 ,sum: 每个日期对应的追踪时间数构成的数组 }
         */
        trackedHoursChartData() {
            const weekRange = this.contextWeekRange;
            const dates = [];
            weekRange.forEach((item) => {
                // 提取出最后的类似11-20的字符串并把它换成11/20这样的数据
                dates.push(item.substring(5).replace("-", "/"));
            });

            // 计算y轴每天的小时数并更新
            const dailySum = [];

            // 指定分类的情况下获取某个分类当日的时间
            if (
                this.filterCat &&
                this.timeEntryCats.find((item) => item.name === this.filterCat.name)
            ) {
                for (const date of weekRange) {
                    let currDayHours = 0;
                    const dateEntries = this.timeEntriesWeeklyOutput.find(
                        (item) => item.date === date
                    );
                    if (dateEntries) {
                        for (const entry of dateEntries.timeEntries) {
                            if (entry.cat === this.filterCat.name) {
                                currDayHours += entry.durationRaw;
                            }
                        }
                    }
                    dailySum.push((currDayHours / 1000 / 60 / 60).toFixed(2));
                }
            } else {
                // 不指定分类的情况获取全部分类每天的时间
                for (const date of weekRange) {
                    const dateEntries = this.timeEntriesWeeklyOutput.find(
                        (item) => item.date === date
                    );
                    if (dateEntries) {
                        dailySum.push((dateEntries.sum / 1000 / 60 / 60).toFixed(2));
                    }
                }
            }
            return {
                dates,
                dailySum,
            };
        },
    },

    watch: {
        contextWeekRange() {
            this.getAllTimeEntriesOfWeek(
                this.contextWeekRange[0],
                this.contextWeekRange[6]
            );
        },

        // 当获取的entry变化时更新展示在页面上的数据
        timeEntriesWeek() {
            console.log(this.timeEntriesWeek);
            let res = [];
            this.contextWeekRange.forEach((date) => {
                res.push({
                    date,
                    timeEntries: [],
                    weekday: this.$timeTool.getWeekDay(date),
                    sum: 0,
                });
            });
            res.reverse(); // 显示的时候每周数据先显示这周最后一天
            this.timeEntriesWeek.forEach((entry) => {
                const e = {};
                if (!this.onlyTimeEntryDurAndDate) {
                    //是否只需要日期和duration
                    e.id = entry.id;
                    e.desc = entry.description || "暂无描述";
                    e.categoryId = entry.categoryId;

                    // 拿到entry对应分类的名字和颜色
                    const cat = this.timeEntryCats.find(
                        (cat) => cat.id === entry.categoryId
                    );
                    e.cat = cat ? cat.name : "默认分类";
                    e.color =
                        cat && cat.color ? cat.color : this.$const.DEFAULT_CAT_COLOR;

                    // 格式化duration
                    e.duration = this.$timeTool.formatDuration(entry.duration);
                    // 标记时间条目是不是小于1分钟
                    if (e.duration.replace(" ", "").includes("0h0m")) {
                        e.duration = "< 1m";
                    }
                    e.durationRaw = entry.duration;
                }
                e.startRaw = entry.start;
                e.endRaw = entry.end;

                //格式化开始和结束日期
                const startTime = this.$timeTool.convertAndFormatDateAndTime(
                    entry.start,
                    this.user.timeFormat
                );
                const endTime = this.$timeTool.convertAndFormatDateAndTime(
                    entry.end,
                    this.user.timeFormat
                );
                e.start = startTime.substring(10);
                e.end = endTime.substring(10);
                e.time = e.start + "-" + e.end;
                const dailyEntries = res.find(
                    (i) => i.date === startTime.substring(0, 10)
                );
                if (dailyEntries) {
                    dailyEntries["timeEntries"].push(e);
                    dailyEntries.sum += entry.duration;
                }
            });
            this.timeEntriesWeeklyOutput = res;

            // 更新每周分类分布
            res = [];
            for (const dailyEntries of this.timeEntriesWeeklyOutput) {
                dailyEntries.timeEntries.forEach((item) => {
                    let currCat = res.find((i) => i.catDesc === item.cat);
                    if (!currCat) {
                        res.push({ catDesc: item.cat, hours: 0, color: item.color });
                        currCat = res[res.length - 1];
                    }
                    currCat.hours += item.durationRaw;
                });
            }
            if (this.weeklyCatHours) {
                this.weeklyCatHours = res;
            }
            if (this.loading && this.loading.timeEntriesWeeklyOutput) {
                this.loading.timeEntriesWeeklyOutput = false;
            }
        },
    },
};