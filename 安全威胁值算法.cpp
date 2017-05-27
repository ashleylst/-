#include <stdio.h>
#include <stdlib.h>
#include<string.h>
struct node
{

    char s1[50],s2[50];//权限
    int sd;//危险系数
    int judge;//判断是closed SD还是unclosed SD
}permission[100];
int main()
{
    freopen("E:in.txt","r",stdin);
	//freopen("E:out.txt","w",stdout);
	int i=0,ans=0,j,h,G;
	memset(permission,0,sizeof(permission));
	scanf("%d",&G);
	while(scanf("%s%s%d",permission[i].s1,permission[i].s2,&permission[i++].sd)!=EOF);
    for(j=0;j<i;j++)
    for(h=j+1;h<i;h++)
    {
        //判断权限组合之间是否相互有联系，即unclosed SD,计算乘积加到结果中
        if((strcmp(permission[j].s1,permission[h].s1)==0||strcmp(permission[j].s1,permission[h].s2)==0||
           strcmp(permission[j].s2,permission[h].s1)==0||strcmp(permission[j].s2,permission[h].s2)==0)
           &&permission[j].sd!=0&&permission[h].sd!=0)
        {
            permission[j].judge=1;
            permission[h].judge=1;
            ans+= permission[j].sd*permission[h].sd;
        }

    }
    //计算closed SD
    for(j=0;j<i;j++)
    {
        if(permission[j].judge==0)
        ans+=permission[j].sd;
    }
    ans*=G;//结果乘上应用权限的分类数量
    printf("Threat point of the application is %d\n",ans);
    return 0;
}
