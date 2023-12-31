import OurTable from "main/components/OurTable";
import { hasRole } from "main/utils/currentUser";

// should take in a players list from a commons
export default function LeaderboardTable({ leaderboardUsers , currentUser }) {

    const columns = [
        {
            Header: 'User Id',
            accessor: 'userId', 
        },
        {
            Header: 'Username',
            accessor: 'username', 
        },
        {
            id : 'totalWealth',
            Header: 'Total Wealth',
            accessor: (leaderboard) => `$${String(leaderboard.totalWealth.toFixed(2))}`,
        },
        {
            Header: 'Cows Owned',
            accessor: 'numOfCows', 
        },
        {
            Header: 'Cow Health',
            accessor: 'cowHealth', 
        },
        {
            Header: 'Cows Bought',
            accessor: 'cowsBought', 
        },
        {
            Header: 'Cows Sold',
            accessor: 'cowsSold', 
        },
        {
            Header: 'Cow Deaths',
            accessor: 'cowDeaths', 
        },
    ];

    const testid = "LeaderboardTable";

    /* Temp filler for admin leaderboard table */

    const columnsIfAdmin = [
        {
            Header: '(Admin) userCommons Id',
            accessor: 'id'
        },
        ...columns

    ];

    const columnsToDisplay = hasRole(currentUser, "ROLE_ADMIN") ? columnsIfAdmin : columns;

    return <OurTable
        data={leaderboardUsers}
        columns={columnsToDisplay}
        testid={testid}
    />;

};